# Use Node.js 22 Alpine as base image
FROM node:22-alpine AS builder

# Set working directory
WORKDIR /app

# Copy package files
COPY package.json package-lock.json* ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Production stage - serve with a lightweight HTTP server
FROM node:22-alpine AS production

# Install serve globally
RUN npm install -g serve

# Set working directory
WORKDIR /app

# Copy built application from builder stage
COPY --from=builder /app/dist ./dist

# Expose port 5173
EXPOSE 5173

# Start the application
CMD ["serve", "-s", "dist", "-l", "5173"]