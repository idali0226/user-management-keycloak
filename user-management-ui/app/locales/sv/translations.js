export default {
    blank: '',
    errors: {
        blank: '{{description}} kan inte vara tom', 
        username: 'Användarnamnet måste vara en giltig e-postadress',
        email: 'Måste vara en giltig e-postadress', 
        'invalid-credentials': 'Username or password is invalid',
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
    'sign-up': "Registrera dig",
    'edit-user': "Edit user",
    'register-new-account': 'Register new account',
    fields: {
        labels: {
            user: {
                'first-name': 'Förnamn',
                'last-name': 'Efternamn',
                name: 'Namn',
                email: 'e-post',
                username: 'Användarnamn', 
                'username-email': 'Användarnamn',
                purpose: 'Syfte',
                'created-date': 'Skapad datum',
                'user-enabled': 'Aktiverad',
                'email-verified': 'E-postadressen har verifierats',
                'account-status': 'Account status',
            },
            client: {
                name: 'Klientnamn',
            }, 
        },
    },
    buttons: {
        labels: {
            'enable-user': 'Enable user',
            'disable-user': 'Disable user',
            'reject-user': 'Reject user',
            'view': 'View',
            'edit-user': 'Edit user',
            'cancel': 'Cancel',
        }
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
            'password-recover': 'Recovery link has been sent to your email address: ', 
            'update-email-action': 'Please click the recovery link to change password within 24 hours.',
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
