#!/usr/bin/env bash

echo "⚙️ Configuring Git Hooks for project..."

echo "> Install Spotless code formatter localy..."
mvn spotless:install-git-pre-push-hook

echo "✅ Git Hooks for project configurated successfully"