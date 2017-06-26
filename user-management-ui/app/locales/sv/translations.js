export default {
    blank: '',
    errors: {
        blank: '{{description}} kan inte vara tom',
        email: 'Användarnamnet måste vara en giltig e-postadress',
    },
    definitions: {
        abort: 'Avbryt',
        continue: 'Fortsätt',
        name: 'Namn',
        date: 'Datum',
        'date-descriptive': 'Datum (åååå-mm-dd)',
        user: 'Användare',
        username: 'Användarnamn',
        password: 'Lösenord',
        usernameOrPassword: 'Användarnamn eller lösenord',
        'sign-out': 'Logga ut',
        'sign-in': 'Logga in',
        'signing-in': 'Loggar in',
        next: 'Nästa',
        previous: 'Föregående',
    },
    main: {
        application_name: 'Användaradministration',
        welcome: 'Välkommen till DINA användaradministration!',
        error: 'Ett fel uppstod, försök igen eller gå till startsidan.',
        'validation-message': {
            save: 'Det gick inte att spara',
        },
    },
    navigation: {
        start: 'Start',
        'users': 'Användare',
        'users.list': 'Lista alla användare',
        'users.add': 'Registrera ny',
        'clients': 'Klienter',
        'clients.list': 'Lista alla klienter',
        'clients.add': 'Skapa ny',
    },
    user: {
        list: {
            header: 'Lista och sök efter användare',
        },
        new: {
            title: 'Ny användare',
            toolbar: {
                save: 'Spara',
                saving: 'Sparar',
                close: 'Stäng',
                duplicate: 'Duplicera',
            },
        },
    },
    'user-profile': 'Användarprofil',
    'sign_up': "Registrera dig",
    fields: {
        labels: {
            user: {
                first_name: 'Förnamn',
                last_name: 'Efternamn',
                name: 'Namn',
                email: 'e-post',
                username: 'Användarnamn',
                purpose: 'Syfte',
                created_date: 'Skapad datum',
                user_enabled: 'Aktiverad',
                email_verified: 'E-postadressen har verifierats',
            },
            client: {
                name: 'Klientnamn',
            },
        },
    },
    client: {
        list: {
            header: 'Lista och sök efter klienter',
        },
        new: {
            title: 'Ny klient',
            toolbar: {
                save: 'Spara',
                saving: 'Sparar',
                print: 'Skriv ut etikett',
                close: 'Stäng',
                duplicate: 'Duplicera',
            },
        },
    },
    beta: {
        header: 'Användaradministration beta',
        intro: ' är en webbplats för att testa och utvärdera det nya systemet för andvändaradministration. Webbplatsen kommer kontinuerligt att uppdateras med nya funktioner och förbättringar enligt interna prioriteringar och den återkoppling som ges efter användartester.',
        feedback: {
            body: 'Använd feeedback-formuläret om du hittar någonting som inte fungerar korrekt eller om du har förbättringsförslag.',
            header: 'Feedback',
            button: 'Skicka feedback',
            'sign-in': 'Logga in för att lämna feedback.',
        },
    }
};
