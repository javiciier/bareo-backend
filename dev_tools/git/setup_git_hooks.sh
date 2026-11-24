#!/usr/bin/env bash

echo "⚙️ Configuring Git Hooks for project..."

HOOK_FILE=".git/hooks/pre-commit"
cat << 'EOF' > "$HOOK_FILE"
#!/usr/bin/env bash
echo "Executing pre-commit checks..."

# Fix static code
echo "Analising code with Spotless..."
MVN_PROFILE=-Ptesting,!development
if ! mvn spotless:apply $MVN_PROFILE; then
  echo 1>&2 "❌ Spotless failed to apply automatic code formatting."
  exit 1
fi

echo "Adding to Git stage the formatted files..."
git add .

echo "Pre-commit checks executed successfully."

exit 0
EOF

# Grant execution permissions to the hook
chmod +x "$HOOK_FILE"
echo "✅ Git Hooks for project configurated successfully"