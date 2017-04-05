export DEPLOY_BRANCH="$TRAVIS_BRANCH"
export DEPLOY_COMMIT="$TRAVIS_COMMIT"
DEPLOY_COMMIT_MESSAGE=$3


file_date=$(date "+%m.%d-%H:%M")


file_name_debug="app/build/outputs/apk/app-debug_$file_date.apk"
file_location_debug="app/build/outputs/apk/app-debug.apk"

echo $file_name_debug
cp $file_location_debug $file_name_debug

SLACK_TEXT="[ *DEBUG* \`$DEPLOY_BRANCH\` | \`$DEPLOY_COMMIT\` ] ${TRAVIS_COMMIT_MESSAGE:-none} "
curl \
  -F "token=$SLACK_KEY" \
  -F "channels=deploy-android" \
  -F "initial_comment=$SLACK_TEXT" \
  -F "file=@$file_name_debug" \
  https://slack.com/api/files.upload


file_name_release="app/build/outputs/apk/app-release_$file_date.apk"
file_location_release="app/build/outputs/apk/app-release.apk"

cp $file_location_release $file_name_release

SLACK_TEXT="[ *RELEASE* \`$DEPLOY_BRANCH\` | \`$DEPLOY_COMMIT\` ] ${TRAVIS_COMMIT_MESSAGE:-none} "
curl \
  -F "token=$SLACK_KEY" \
  -F "channels=deploy-android" \
  -F "initial_comment=$SLACK_TEXT" \
  -F "file=@$file_name_release" \
  https://slack.com/api/files.upload