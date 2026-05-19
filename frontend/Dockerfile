# ── Stage 1 : Build ───────────────────────────────────────────
FROM node:22-alpine AS build
WORKDIR /app

# Dépendances d'abord (cache Docker si package.json inchangé)
COPY package*.json ./
RUN npm install -g npm@latest --quiet && npm ci --ignore-scripts

# Code source + build Nuxt
COPY . .
RUN npm run build

# ── Stage 2 : Runtime ─────────────────────────────────────────
FROM node:22-alpine AS runtime
WORKDIR /app

# Utilisateur non-root
RUN addgroup -S samar && adduser -S samar -G samar

COPY --from=build /app/.output ./.output
RUN chown -R samar:samar .

USER samar

EXPOSE 3000

ENV NODE_ENV=production

CMD ["node", ".output/server/index.mjs"]
