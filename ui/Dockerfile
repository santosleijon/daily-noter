# Step 1: Build stage
FROM node:lts-alpine AS builder
WORKDIR /app

# Force clean installation
RUN rm -rf node_modules package-lock.json

COPY package*.json ./

RUN npm install --frozen-lockfile

COPY . .

# Fix issue with "tsc: Permission denied" error
RUN chmod -R +x /app/node_modules

RUN npm run build

# Step 2: Run stage with nginx
FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 8081

RUN sed -i 's/listen       80;/listen 8081;/g' /etc/nginx/conf.d/default.conf
RUN sed -i 's/listen  [::]:80;/listen [::]:8081;/g' /etc/nginx/conf.d/default.conf

CMD ["nginx", "-g", "daemon off;"]