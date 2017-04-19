#get commit info 
if [ -z "$TRAVIS_BRANCH" ]
  then
    echo "deploy in local"
    DEPLOY_BRANCH=$(git symbolic-ref --short HEAD)
    DEPLOY_COMMIT=$(git log --pretty=format:'%h' -n 1)
    DEPLOY_COMMIT_MESSAGE=$(git log -1 --pretty=%B)
  else
    echo "deploy in travis"
    DEPLOY_BRANCH="$TRAVIS_BRANCH"
    DEPLOY_COMMIT=$(git log --pretty=format:'%h' -n 1)
    DEPLOY_COMMIT_MESSAGE=$(git log -1 --pretty=%B)
fi

echo $DEPLOY_BRANCH
echo $DEPLOY_COMMIT
echo $DEPLOY_COMMIT_MESSAGE


#: <<'END'

#debug
filename="$(find . -name app-debug_*.apk)"

SLACK_TEXT="[ *DEBUG* \`$DEPLOY_BRANCH\` | \`$DEPLOY_COMMIT\` ] ${DEPLOY_COMMIT_MESSAGE:-none} "
curl \
  -F "token=$SLACK_KEY" \
  -F "channels=deploy-android" \
  -F "initial_comment=$SLACK_TEXT" \
  -F "file=@$filename" \
  https://slack.com/api/files.upload

#release
filename="$(find . -name app-release_*.apk)"

SLACK_TEXT="[ *RELEASE* \`$DEPLOY_BRANCH\` | \`$DEPLOY_COMMIT\` ] ${DEPLOY_COMMIT_MESSAGE:-none} "
curl \
  -F "token=$SLACK_KEY" \
  -F "channels=deploy-android" \
  -F "initial_comment=$SLACK_TEXT" \
  -F "file=@$filename" \
  https://slack.com/api/files.upload

#product
if [ "$DEPLOY_BRANCH" = "master" ]; then
  filename="$(find . -name app-product_*.apk)"

  SLACK_TEXT="[ *PRODUCT* \`$DEPLOY_BRANCH\` | \`$DEPLOY_COMMIT\` ] ${DEPLOY_COMMIT_MESSAGE:-none} "
  curl \
    -F "token=$SLACK_KEY" \
    -F "channels=deploy-android" \
    -F "initial_comment=$SLACK_TEXT" \
    -F "file=@$filename" \
    https://slack.com/api/files.upload
fi

#END
