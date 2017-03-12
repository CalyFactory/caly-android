./gradlew build -stacktrace


DEPLOY_BRANCH=$1
DEPLOY_COMMIT=$2
DEPLOY_COMMIT_MESSAGE=$3

SLACK_KEY="xoxb-118976894853-""ypenVhWFgnlrfGmHXhrsImeq"
SLACK_TEXT="[ `$DEPLOY_BRANCH` | `$DEPLOY_COMMIT` ] *$DEPLOY_COMMIT_MESSAGE* ${TRAVIS_COMMIT_MESSAGE:-none} "
echo $SLACK_TEXT
echo $TRAVIS_BRANCH

echo $DEPLOY_BRANCH
echo $DEPLOY_COMMIT
echo $DEPLOY_COMMIT_MESSAGE
curl \
  -F "token=$SLACK_KEY" \
  -F "channel=deploy-android" \
  -F "text=$SLACK_TEXT" \
  https://slack.com/api/chat.postMessage