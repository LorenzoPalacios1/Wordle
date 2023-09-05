#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

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

void intro(void)
{
    puts("Welcome to Wordle!\n"
         "Here are some basic instructions:");
    printf("A \"%c\" beneath your guess means the character above it is correct and in the right position.\n", CORRECT_INDICATOR);
    printf("A \"%c\" beneath your guess means that the character above it is present in the Wordle, but in the wrong position.\n", BAD_CHAR_POS_INDICATOR);
    printf("Finally, a \"%c\" beneath your guess means that the character above it is NOT in the Wordle.\n", WRONG_CHAR_INDICATOR);

    printf("\nAnd that's it!\n"
           "Oh, and by the way, the Wordle is a string of %llu random characters, so it's probably not even an actual word!\n"
           "So, go ahead and...\n\n",
           WORDLE_LENGTH);
}

// This function will take user's guess, compare it to the Wordle, print out helpful
// indicators to the player, and return whether or not their guess was correct.
// \returns True if the guess matches the Wordle exactly, otherwise false.
char interpret_guess(const char *wordle, const char *guess)
{
    if (strcmp(wordle, guess) == 0)
        return 1;

    puts(guess);
    for (size_t i = 0; i < strlen(guess); i++)
    {
        const char *char_ptr_wordle = strchr(wordle, guess[i]);
        const char *char_ptr_guess = strchr(guess, wordle[i]);
        if (char_ptr_wordle == NULL)
            putchar(WRONG_CHAR_INDICATOR);
        else if (char_ptr_wordle != char_ptr_guess)
            putchar(BAD_CHAR_POS_INDICATOR);
        else
            putchar(CORRECT_INDICATOR);
    }
    putchar('\n');

    return 0;
}

int main(void)
{
    srand(time(NULL));
    const char *wordle = generate_wordle(WORDLE_LENGTH);
    intro();
    puts(wordle);
    static char plr_guess[WORDLE_LENGTH + 1];
    for (int i = 0; i <= MAX_PLR_GUESSES; i++)
    {
        printf("Enter your guess: ");
        // Getting the player's guess
        for (size_t i = 0; i < sizeof(plr_guess) - 1; i++)
        {
            if ((plr_guess[i] = getchar()) == '\n')
            {
                plr_guess[i] = '\0';
                break;
            }
        }
        plr_guess[sizeof(plr_guess) - 1] = '\0';
        // fseek() here to clear any unused input
        fseek(stdin, 0, SEEK_END);

        if (interpret_guess(wordle, plr_guess))
        {
            i == 0 ? printf("Wow! You managed to guess \"%s\" in just 1 try!\n", wordle) : printf("You did it! You managed to guess \"%s\" in %llu tries!\n", wordle, i + 1);
            break;
        }

        i == MAX_PLR_GUESSES - 1 ? puts("1 guess left!\n") : printf("%d guesses left!\n", MAX_PLR_GUESSES - i);
    }
    free((char *)wordle);
    return 0;
}
