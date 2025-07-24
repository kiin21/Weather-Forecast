#!/bin/bash
# filepath: /home/khoa/Projects/Springboot/WorldCast/run.sh

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

# Helper functions
info() { echo -e "${BLUE}[INFO]${NC} $1"; }
success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

# Cleanup function
cleanup() {
    info "Shutting down..."
    [[ ! -z "$BACKEND_PID" ]] && kill $BACKEND_PID 2>/dev/null
    [[ ! -z "$FRONTEND_PID" ]] && kill $FRONTEND_PID 2>/dev/null
    exit 0
}
trap cleanup SIGINT SIGTERM

# Check dependencies
command -v mvn >/dev/null 2>&1 || error "Maven not found"
command -v npm >/dev/null 2>&1 || error "npm not found"

# Set directories
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/WorldCast-Backend"
FRONTEND_DIR="$SCRIPT_DIR/WorldCast-Frontend"

[[ ! -d "$BACKEND_DIR" ]] && error "Backend directory not found"
[[ ! -d "$FRONTEND_DIR" ]] && error "Frontend directory not found"

success "Starting WorldCast Application"

# Start backend
info "Starting Spring Boot backend..."
cd "$BACKEND_DIR" && mvn spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!
sleep 3
kill -0 $BACKEND_PID 2>/dev/null || error "Backend failed to start. Check backend.log"
success "Backend started (PID: $BACKEND_PID)"

# Start frontend
info "Starting frontend..."
cd "$FRONTEND_DIR"
[[ ! -d "node_modules" ]] && info "Installing dependencies..." && npm install
npm run start > frontend.log 2>&1 &
FRONTEND_PID=$!
sleep 3
kill -0 $FRONTEND_PID 2>/dev/null || { error "Frontend failed to start. Check frontend.log"; cleanup; }
success "Frontend started (PID: $FRONTEND_PID)"

success "✨ WorldCast is running!"
info "Backend: http://localhost:8080"
info "Frontend: http://localhost:5173"
info "Press Ctrl+C to stop"

wait