#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include "MyBasics.h"

#define MAX_PLR_GUESSES 6
#define WORDLE_LENGTH 5

// These are characters that will appear beneath the player's guess
// telling them what they got right, half-right, and wrong
#define CORRECT_INDICATOR ' '
#define BAD_CHAR_POS_INDICATOR '*'
#define WRONG_CHAR_INDICATOR '^'

// \returns A string containing 'length' pseudo-randomly generated alphabetical characters.
char *generate_wordle(const int length)
{
    char *new_wordle = malloc((length + 1) * sizeof(char));
    for (int i = 0; i < length; i++)
    {
        new_wordle[i] = rand() % ('z' - 'a' + 1) + 'a';
    }
    new_wordle[length] = '\0';
    return new_wordle;
}

static char *wordle = NULL;

void intro(void)
{
    srand(time(NULL));

    wordle = generate_wordle(WORDLE_LENGTH);

    puts("Welcome to Wordle!\n"
        "Here are some basic instructions:");
    printf("A \"%c\" beneath your guess means the character above it is correct and in the right position.\n", CORRECT_INDICATOR);
    printf("A \"%c\" beneath your guess means that the character above it is present in the Wordle, but in the wrong position.\n", BAD_CHAR_POS_INDICATOR);
    printf("Finally, a \"%c\" beneath your guess means that the character above it does NOT exist in the Wordle.\n", WRONG_CHAR_INDICATOR);

    puts("\nAnd that's it!"
         "Oh, and by the way, the Wordle is a string of random characters, so it's probably not an actual word!"
         "Have fun!\n\n");
}

int main(void)
{
    intro();

    puts(wordle);

    for (int i = 0; i < MAX_PLR_GUESSES; i++)
    {
        printf("Enter your guess: ");
        const char *plr_guess = getStrStdin(WORDLE_LENGTH);
        if (strcasecmp(wordle, plr_guess))
        {
            puts("d");
        }
        // Type-casting to a non-const char* to silence a warning
        free((char *)plr_guess);
    }

    return 0;
}
