#!/usr/bin/env bash

git_config() {
    local property="$1"
    local value="$2"

    git config "$property" "$value"
    printf "\tSetting $property \t=\t $value\n"
}

echo "⚙️ Configuring Git properties for the repository..."

# ----- CORE configuration -----
# Configure LF line endings for all platforms
git_config core.autocrlf input
# Show whitespaces on diffs
git_config core.whitespace trailing-space
# Configure default branch name to main
git_config init.defaultBranch main


# Configure all existing branches to use rebase by default
git_config branch.autosetuprebase always
# Sort branches by recent activity
git_config branch.sort -committerdate
# Colorize git output
git_config color.ui 1
# Sign commits
git_config commit.gpgSign true
# Improve diffs between commits
git_config diff.algorithm histogram
# Stop tracking deled branches and tags
git_config fetch.prune true
git_config fetch.prunetags
# Autocorrect typos in git commands
git_config help.autocorrect 10
# Display commit dates in ISO format
git_config log.dateFormat iso
# Set rebase as default merge strategy
git_config pull.rebase true
# Track remote and local branches with the same name
git_config push.autoSetupRemote true
# Include tags with push commit
git_config push.followtags true
# Auto-squash commits when rebasing
git_config rebase.autosquash true
# Stash commits when rebasing
git_config rebase.autostash true
# Avoid deleting commits during a rebase
git_config rebase.missingCommitsCheck error
# Rebase stacked branches at once
git_config rebase.updateRefs true
# Reuse previous merge conflict solutions when resolving merge conflicts
git_config rerere.autoupdate true
git_config rerere.enabled true
# Sort tags by releae version
git_config tag.sort version:refname

echo "✅ Git configuration completed successfully!"