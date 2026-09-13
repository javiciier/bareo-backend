#!/usr/bin/env bash

echo "⚙️ Configuring Git Hooks for project..."

HOOK_FILE=".git/hooks/pre-commit"
cat << 'EOF' > "$HOOK_FILE"
#!/usr/bin/env bash
echo "Executing pre-commit checks..."

# Fix static code
echo "Analising code with Spotless..."
MVN_PROFILE=-Pdevelopment
if ! mvn $MVN_PROFILE spotless:check; then
  echo 1>&2 "❌ Spotless has found problems. Trying to apply automatic fixes"
  mvn spotless:apply MVN_PROFILE
  exit 1
fi

echo "Pre-commit checks executed successfully."
EOF

# Grant execution permissions to the hook
chmod +x "$HOOK_FILE"
echo "✅ Git Hooks for project configurated successfully"