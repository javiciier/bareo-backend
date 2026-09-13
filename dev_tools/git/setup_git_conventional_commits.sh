#!/usr/bin/env bash

echo "⚙️ Configuring Git Conventional Commits convention for project..."

HOOK_FILE=".git/hooks/commit-msg"
cat << 'EOF' > "$HOOK_FILE"
#!/usr/bin/env bash
commit_msg_file="$1"
commit_msg=$(cat "$commit_msg_file")

# Format: type(scope): subject
regex="^(feat|fix|docs|style|refactor|perf|test|chore|ci|build|revert)(\(.+\))?!?: .+$"

if ! echo "$commit_msg" | grep -qE "$regex"; then
  echo "❌ Error: Commit message does not acomplish with Conventional Commits standard."
  echo ""
  echo "ℹ️ Required format: <type>(<optional scope>): <subject>"
  echo "   Valid example: feat(authentication): add login validations"
  echo "   Valid types: feat, fix, docs, style, refactor, perf, test, chore, ci, build, revert"
  exit 1
fi
EOF

# Grant execution permissions to the hook
chmod +x "$HOOK_FILE"
echo "✅ Conventional commits configured succesfully"