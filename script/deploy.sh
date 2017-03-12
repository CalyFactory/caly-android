SLACK_KEY="xoxb-118976894853-""ypenVhWFgnlrfGmHXhrsImeq"
SLACK_TEXT="[ `${TRAVIS_BRANCH}` |` ${TRAVIS_COMMIT}` ] *$TRAVIS_COMMIT_MESSAGE* "
echo $SLACK_TEXT
echo $TRAVIS_BRANCH
curl \
  -F "token=$SLACK_KEY" \
  -F "channels=deploy-android" \
  -F "initial_comment=$SLACK_TEXT" \
  -F "file=@app/build/outputs/apk/app-debug.apk" \
  https://slack.com/api/files.upload