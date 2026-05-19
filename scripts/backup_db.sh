#!/bin/bash
# PBS PostgreSQL backup script
# Dumps the prod DB from the running container and uploads to S3
# Runs via cron on VPS — activate only when prod stack is deployed
#
# Required env vars (set in /etc/environment or a sourced .env):
#   PBS_DB_CONTAINER  — container name (default: pbs_db)
#   DB_NAME           — database name
#   DB_USERNAME       — postgres user
#   AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, AWS_DEFAULT_REGION
#   S3_BUCKET         — e.g. s3://my-bucket/pbs-backups

set -euo pipefail

CONTAINER="${PBS_DB_CONTAINER:-pbs_db}"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="/tmp/pbs_backup_${TIMESTAMP}.sql.gz"
S3_PATH="${S3_BUCKET}/pbs_backup_${TIMESTAMP}.sql.gz"
RETENTION_DAYS=30

log() { echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*"; }

log "Starting PBS DB backup — container: $CONTAINER"

# Dump and compress
docker exec "$CONTAINER" pg_dump -U "$DB_USERNAME" "$DB_NAME" | gzip > "$BACKUP_FILE"
log "Dump complete: $BACKUP_FILE ($(du -sh "$BACKUP_FILE" | cut -f1))"

# Upload to S3
aws s3 cp "$BACKUP_FILE" "$S3_PATH"
log "Uploaded to $S3_PATH"

# Cleanup local file
rm -f "$BACKUP_FILE"

# Delete S3 backups older than RETENTION_DAYS
log "Cleaning S3 backups older than ${RETENTION_DAYS} days"
aws s3 ls "${S3_BUCKET}/" | awk '{print $4}' | while read -r key; do
  file_date=$(echo "$key" | grep -oP '\d{8}')
  if [[ -n "$file_date" ]]; then
    cutoff=$(date -d "${RETENTION_DAYS} days ago" +%Y%m%d)
    if [[ "$file_date" < "$cutoff" ]]; then
      aws s3 rm "${S3_BUCKET}/${key}"
      log "Deleted old backup: $key"
    fi
  fi
done

log "Backup complete"
