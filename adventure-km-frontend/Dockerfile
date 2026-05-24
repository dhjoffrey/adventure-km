FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npx ng build --configuration=production

FROM node:20-alpine
WORKDIR /app
COPY --from=build /app/dist/adventure-km-frontend ./dist/adventure-km-frontend
ENV PORT=4000
EXPOSE 4000
CMD ["node", "dist/adventure-km-frontend/server/server.mjs"]
