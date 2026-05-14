FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
ARG API_URL=http://localhost:8080/api
RUN sed -i "s|__API_URL__|${API_URL}|g" src/environments/environment.prod.ts && npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist/gestion-retours-frontend/browser /usr/share/nginx/html
EXPOSE 80
