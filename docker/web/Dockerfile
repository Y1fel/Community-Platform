FROM node:22.18-alpine AS resident-build

WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci --no-audit --no-fund
COPY frontend/ ./

ARG VITE_API_BASE_URL=/api
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
RUN npm run build -- --base=/h5/

FROM node:22.18-alpine AS admin-build

WORKDIR /app
COPY untitled/package*.json ./
RUN npm ci --no-audit --no-fund
COPY untitled/ ./
RUN npm run build

FROM nginx:1.27-alpine

COPY docker/nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=resident-build /app/dist/ /usr/share/nginx/html/h5/
COPY --from=admin-build /app/dist/ /usr/share/nginx/html/

EXPOSE 80
