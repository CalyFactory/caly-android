./gradlew clean build \
-Pandroid.injected.signing.store.file="travis-encrypt/calysigningkey.jks" \
-Pandroid.injected.signing.store.password=$STORE_PASSWORD \
-Pandroid.injected.signing.key.alias=$KEY_ALIAS \
-Pandroid.injected.signing.key.password=$KEY_PASSWORD

file_date=$(date "+%m.%d-%H:%M")

#debug 
file_name_debug="app/build/outputs/apk/app-debug_$file_date.apk"
file_location_debug="app/build/outputs/apk/app-debug.apk"

cp $file_location_debug $file_name_debug

#release
file_name_release="app/build/outputs/apk/app-release_$file_date.apk"
file_location_release="app/build/outputs/apk/app-release.apk"

cp $file_location_release $file_name_release

#product
file_name_product="app/build/outputs/apk/app-product_$file_date.apk"
file_location_product="app/build/outputs/apk/app-product.apk"

cp $file_location_product $file_name_product