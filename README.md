# Biometria

Biometria is an Android app that uses biometric authentication to send an encrypted device ID to a server for verification.

## Features

- Biometric authentication using fingerprint
- Encrypted device ID using MD5 hash
- Custom authentication code
- Communication with server over socket

## Usage

On app startup:

- Enter server IP and port number
- Enter authentication code (optional)
- Click authenticate button to trigger biometric prompt
- Scan fingerprint to authenticate

If authentication succeeds:

- Encrypted device ID is sent to server
- A reply message is received and displayed 

## Code Overview

- `MainActivity` - Main app UI and logic
- `BiometricPrompt` - Handles biometric authentication 
- `Encrypt` - Generates MD5 hash of device ID  
- `Communication` - Data class for client/server communication
- `Server` - Socket client to send/receive data from server

## Setup

This app requires:

- Biometric hardware like fingerprint scanner
- Server socket listening on configured IP and port
- Server logic to handle requests and send response

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://choosealicense.com/licenses/mit/)
