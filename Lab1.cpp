#include <iostream>
#include <string>
#include <cctype>

using namespace std;

string VigenereCipher(string text, string key, bool encrypt = true) {
    string result = "";
    string Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 !@#$%^&*()_+-=[]{}|/;':,.<>?";
    int allowedCharsSize = Chars.length();
    int textLength = text.length();
    int keyLength = key.length();

    for (int i = 0; i < textLength; ++i) {
        char currentChar = text[i];
        char keyChar = key[i % keyLength];

        int currentCharIndex = Chars.find(currentChar);
        int keyCharIndex = Chars.find(keyChar);

        if (currentCharIndex != string::npos) {
            int modifier = encrypt ? 1 : -1;
            int encryptedCharIndex = (currentCharIndex + modifier * keyCharIndex + allowedCharsSize) % allowedCharsSize;

            result += Chars[encryptedCharIndex];
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
    getline(cin, key);

    string encryptedText = VigenereCipher(Text, key, true);
    cout << "\nEncrypted text: " << encryptedText << endl;

    string decryptedText = VigenereCipher(encryptedText, key, false);
    cout << "Decrypted text: " << decryptedText << endl;

    return 0;
}
