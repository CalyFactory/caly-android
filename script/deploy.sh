echo $1
echo $2
echo $3
BRANCH_NAME=$1
COMMIT_TAG=$2
COMMIT_MSG=$3
SLACK_KEY="xoxb-118976894853-""ypenVhWFgnlrfGmHXhrsImeq"
SLACK_TEXT="[ `$BRANCH_NAME` |` $COMMIT_TAG` ] *$COMMIT_MSG* "
echo $SLACK_TEXT
curl \
  -F "token=$SLACK_KEY" \
  -F "channels=deploy-android" \
  -F "initial_comment=$SLACK_TEXT" \
  -F "file=@app/build/outputs/apk/app-debug.apk" \
  https://slack.com/api/files.upload