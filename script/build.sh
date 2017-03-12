echo $1
SLACK_KEY="xoxb-118976894853-""ypenVhWFgnlrfGmHXhrsImeq"
echo $SLACK_KEY
curl \
  -F "token=$SLACK_KEY" \
  -F "channels=testbed" \
  -F "file=@./app/build/outputs/apk/app-debug.apk" \
  https://slack.com/api/files.upload