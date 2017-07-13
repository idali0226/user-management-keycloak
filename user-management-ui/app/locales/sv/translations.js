export default {
    blank: '',
    errors: {
        blank: '{{description}} kan inte vara tom', 
        username: 'Användarnamnet måste vara en giltig e-postadress',
        email: 'Måste vara en giltig e-postadress', 
    },
    definitions: {
        abort: 'Avbryt',
        continue: 'Fortsätt',
        name: 'Namn',
        date: 'Datum',
        'date-descriptive': 'Datum (åååå-mm-dd)',
        user: 'Användare',
        username: 'Användarnamn',
        'username-email': 'Username/email',
        email: 'Email',
        password: 'Lösenord',
        usernameOrPassword: 'Användarnamn eller lösenord',
        'sign-out': 'Logga ut',
        'sign-in': 'Logga in',
        'signing-in': 'Loggar in',
        send: 'Send',
        sending: 'Sending',
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
        password_recover: 'Forgot password?',
        register: 'Register',
        profile: 'Profile', 
        users: 'Användare',
        'users.list': 'Lista alla användare',
        'users.add': 'Registrera ny',
        clients: 'Klienter',
        'clients.list': 'Lista alla klienter',
        'clients.add': 'Skapa ny',
    }, 
    account: {
        new: {
            title: 'Create account',
        }
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
    'edit_user': "Edit user",
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
    messages: {
        account: {
            'create-account': 'Your User Management account is created.',
            'verification-email': 'A verification email will be sent to: ',
            'email-verification-action': 'Please click the activation link to verifiy your email address within 24 hours',
        }, 
        'password-recover': {
            'change-password': 'Your password has been changed.',
            'update-password-email': 'An update email will be sent to:',
            'update-email-action': 'Please click the update password link to update email',
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
