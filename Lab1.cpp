#include <iostream>
#include <string>
#include <cctype>

using namespace std;


string VigenereCipherASCII(string text, string key, bool encrypt = true) {
    string result = "";
    int keyLength = key.length();

    for (int i = 0, j = 0; i < text.length(); ++i) {
        char currentChar = text[i];

        if (currentChar >= 32 && currentChar <= 127) {
            int textIndex = int(currentChar) - 32;
            int keyIndex = int(toupper(key[j % keyLength])) - 32;

            if (encrypt) {
                result += char((textIndex + keyIndex) % 96 + 32);
            }
            else {
                result += char((textIndex - keyIndex + 96) % 96 + 32);
            }
            ++j;
        }
        else {
            result += currentChar;
        }
    }
    return result;
}


string VigenereCipherArray(string text, string key, bool encrypt = true) {
    string result = "";
    int keyLength = key.length();
    string alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ";

    for (int i = 0, j = 0; i < text.length(); ++i) {
        char currentChar = text[i];

        if (isalpha(currentChar)) {
            char base = isupper(currentChar) ? 'A' : 'a';
            int textIndex = currentChar - base;
            int keyIndex = toupper(key[j % keyLength]) - 'A';

            if (encrypt) {
                result += alphabet[(textIndex + keyIndex) % 52];
            }
            else {
                result += alphabet[(textIndex - keyIndex + 52) % 52];
            }
            ++j;
        }
        else {
            result += currentChar;
        }
    }
    return result;
}

int main() {
    string input, key;

    cout << "Enter the text: ";
    getline(cin, input);

    cout << "Enter the key: ";
    getline(cin, key);

    string encryptedTextASCII = VigenereCipherASCII(input, key, true);
    cout << "Encrypted Text (ASCII): " << encryptedTextASCII << endl;

    string decryptedTextASCII = VigenereCipherASCII(encryptedTextASCII, key, false);
    cout << "Decrypted Text (ASCII): " << decryptedTextASCII << endl;

    string encryptedTextArray = VigenereCipherArray(input, key, true);
    cout << "Encrypted Text (Array): " << encryptedTextArray << endl;

    string decryptedTextArray = VigenereCipherArray(encryptedTextArray, key, false);
    cout << "Decrypted Text (Array): " << decryptedTextArray << endl;

    return 0;
}
