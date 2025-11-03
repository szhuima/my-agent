# --------------------------
# Stage 1: Base Node Image
# --------------------------
FROM registry.cn-hangzhou.aliyuncs.com/xfg-studio/node:20-alpine AS base

# --------------------------
# Stage 2: Install dependencies
# --------------------------
FROM base AS deps
RUN apk add --no-cache libc6-compat
WORKDIR /app

# Copy dependency definitions
COPY package.json yarn.lock* package-lock.json* pnpm-lock.yaml* ./

# Use npm mirror for faster downloads in China
RUN yarn config set registry 'https://registry.npmmirror.com/'
RUN yarn install --frozen-lockfile

# --------------------------
# Stage 3: Build project
# --------------------------
FROM base AS builder
WORKDIR /app

# Copy dependencies
COPY --from=deps /app/node_modules ./node_modules
# Copy source code
COPY . .

# Build the project
RUN npx rsbuild build

# --------------------------
# Stage 4: Production Nginx Image
# --------------------------
FROM registry.cn-hangzhou.aliyuncs.com/xfg-studio/nginx:1.25.1 AS runner
WORKDIR /usr/share/nginx/html

# Clear default assets
RUN rm -rf ./*

# Copy built static files
COPY --from=builder /app/dist .

# Copy optimized Nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 3002

# Start Nginx in foreground
CMD ["nginx", "-g", "daemon off;"]
