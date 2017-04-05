rm app/build/outputs/apk/*

file_date=$(date "+%m.%d-%H:%M")

#debug 
./gradlew assembleDebug \
-Pandroid.injected.signing.store.file="travis-encrypt/calysigningkey.jks" \
-Pandroid.injected.signing.store.password=$STORE_PASSWORD \
-Pandroid.injected.signing.key.alias=$KEY_ALIAS \
-Pandroid.injected.signing.key.password=$KEY_PASSWORD

file_name_debug="app/build/outputs/apk/app-debug_$file_date.apk"
file_location_debug="app/build/outputs/apk/app-debug.apk"

cp $file_location_debug $file_name_debug


#release
./gradlew assembleRelease \
-Pandroid.injected.signing.store.file="travis-encrypt/calysigningkey.jks" \
-Pandroid.injected.signing.store.password=$STORE_PASSWORD \
-Pandroid.injected.signing.key.alias=$KEY_ALIAS \
-Pandroid.injected.signing.key.password=$KEY_PASSWORD

file_name_release="app/build/outputs/apk/app-release_$file_date.apk"
file_location_release="app/build/outputs/apk/app-release.apk"

cp $file_location_release $file_name_release