#include <iostream>
#include <string>

using namespace std;

string VigenereCipher(string text, string key, bool encrypt = true) {
    string result = "";
    string alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    string numbers = "0123456789";
    string specialChars = "!@#$%^&*()_+-=[]{}|/;':,.<>?";
    int alphabetSize = alphabet.length();
    int textLength = text.length();
    int keyLength = key.length();

    for (int i = 0; i < textLength; ++i) {
        char currentChar = text[i];
        char keyChar = key[i % keyLength];

        keyChar = isupper(keyChar) ? toupper(keyChar) : tolower(keyChar);

        int currentCharIndex = -1;
        int keyCharIndex = -1;

        if (isalpha(currentChar)) {
            currentCharIndex = alphabet.find(currentChar);
            keyCharIndex = alphabet.find(keyChar);
        }
        else if (isdigit(currentChar)) {
            currentCharIndex = numbers.find(currentChar);
            keyCharIndex = numbers.find(keyChar);
        }
        else {
            currentCharIndex = specialChars.find(currentChar);
            keyCharIndex = specialChars.find(keyChar);
        }

        if (currentCharIndex != string::npos) {
            int modifier = encrypt ? 1 : -1;
            int encryptedCharIndex = (currentCharIndex + modifier * keyCharIndex + alphabetSize) % alphabetSize;

            if (isalpha(currentChar)) {
                result += isupper(currentChar) ? toupper(alphabet[encryptedCharIndex]) : tolower(alphabet[encryptedCharIndex]);
            }
            else if (isdigit(currentChar)) {
                result += numbers[encryptedCharIndex];
            }
            else {
                result += specialChars[encryptedCharIndex];
            }
        }
        else {
            result += currentChar;
        }
    }

    return result;
}

int main() {
    string Text, key;

    cout << "Enter the text: ";
    getline(cin, Text);

    cout << "Enter the key: ";
    cin >> key;

    string encryptedText = VigenereCipher(Text, key, true);
    string decryptedText = VigenereCipher(encryptedText, key, false);

    cout << "\nEncrypted text: " << encryptedText << endl;
    cout << "Decrypted text: " << decryptedText << endl;

    return 0;
}
