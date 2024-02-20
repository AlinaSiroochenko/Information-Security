#include <iostream>
#include <string>
#include <cctype>

using namespace std;

string VigenereCipher(string text, string key, bool encrypt = true) {
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

int main() {
    string input, key;

    cout << "Enter the text: ";
    getline(cin, input);

    cout << "Enter the key: ";
    getline(cin, key);

    string encryptedText = VigenereCipher(input, key, true);
    cout << "Encrypted Text: " << encryptedText << endl;

    string decryptedText = VigenereCipher(encryptedText, key, false);
    cout << "Decrypted Text: " << decryptedText << endl;

    return 0;
}
