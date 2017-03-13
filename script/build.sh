

DEPLOY_BRANCH=${1}
DEPLOY_COMMIT=${2}
DEPLOY_COMMIT_MESSAGE=$3

SLACK_KEY="xoxb-118976894853-""ypenVhWFgnlrfGmHXhrsImeq"
SLACK_TEXT="[ `{$DEPLOY_BRANCH}` | `{$DEPLOY_COMMIT}` ] *$DEPLOY_COMMIT_MESSAGE* ${TRAVIS_COMMIT_MESSAGE:-none} "
echo $SLACK_TEXT
echo $TRAVIS_BRANCH

echo "branch {$DEPLOY_BRANCH} branch"
echo $DEPLOY_COMMIT
echo $DEPLOY_COMMIT_MESSAGE
echo "branch name `{$DEPLOY_BRANCH}`  "
echo "branch name ${$DEPLOY_BRANCH}  "
echo "branch name ${$BUILD_BRANCH_NAME}  "
echo "branch name {$BUILD_BRANCH_NAME}  "
curl \
  -F "token=$SLACK_KEY" \
  -F "channel=deploy-android" \
  -F "text={$DEPLOY_BRANCH}  {$DEPLOY_COMMIT} *$DEPLOY_COMMIT_MESSAGE* ${TRAVIS_COMMIT_MESSAGE:-none} " \
  https://slack.com/api/chat.postMessage


./gradlew build -stacktrace
