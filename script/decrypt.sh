openssl aes-256-cbc -K $encrypted_27342c7446d8_key -iv $encrypted_27342c7446d8_iv -in travis-encrypt.tgz.enc -out ./travis-encrypt.tgz -d
tar xfvz travis-encrypt.tgz
cp travis-encrypt/google-services.json app/google-services.json