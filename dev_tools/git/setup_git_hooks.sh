#!/usr/bin/env bash

echo "⚙️ Configuring Git Hooks for project..."

HOOK_FILE=".git/hooks/pre-commit"
cat << 'EOF' > "$HOOK_FILE"
#!/usr/bin/env bash
echo "Executing pre-commit checks..."

# Fix static code
echo "Analising code with Spotless..."
MVN_PROFILE=-Ptesting,!development
if ! mvn $MVN_PROFILE spotless:check; then
  echo 1>&2 "❌ Spotless has found problems. Trying to apply automatic fixes"
  mvn spotless:apply $MVN_PROFILE
fi

echo "Pre-commit checks executed successfully."

exit 0
EOF

# Grant execution permissions to the hook
chmod +x "$HOOK_FILE"
echo "✅ Git Hooks for project configurated successfully"