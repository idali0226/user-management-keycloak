export default {
    blank: '',
    errors: {
        blank: '{{description}} can\'t be blank',
        email: 'Username must be a valid email address', 
    },
    definitions: {
        abort: 'Avbryt',
        continue: 'Fortsätt',
        name: 'Namn',  
        date: 'Datum',
        'date-descriptive': 'Datum(åååå-mm-dd)',
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
        application_name: 'User management',
        welcome: 'Välkommen till DINA User Management System!',
        error: 'Ett fel uppstod, försök igen eller gå till startsidan.',
        'validation-message': {
            save: 'Det gick inte att spara',
        },
    }, 
    navigation: {
        start: 'Start', 
        'users': 'Användare',
        'users.list': 'Lista alla användare',
        'users.add': 'Registrera nytt',
        'clients': 'Clients',
        'clients.list': 'Lista alla clients',
        'clients.add': 'Registrera nytt',
    },
    user: { 
        list: {
            header: 'Lista och söka efter användare',
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
    'user-profile': 'User profile',
    'sign_up': "Sign up",
    fields: {
        labels: {
            user: {
                first_name: 'Förnamn',
                last_name: 'Efternamn',
                name: 'Namn',
                email: 'e-post',
                username: 'Användarnamn',
                purpose: 'Purpose',
                created_date: 'Created date',
                user_enabled: 'Enabled',
                email_verified: 'Email verified',
            }, 
            client: { 
                name: 'Client name', 
            }, 
        },
    },   
    client: { 
        list: {
            header: 'List and search for clients',
        },
        new: { 
            title: 'New client', 
            toolbar: {
                save: 'Save',
                saving: 'Saving',
                print: 'Print label',
                close: 'Close',
                duplicate: 'Duplicate',
            },
        },
    }, 
    beta: {
        header: 'User management beta',
        intro: ' is a test site to evaluate the new user management system. The site will be continuously updated with new features and fixes based on the internal priority and feedback from user testing.',
        feedback: {
            body: 'Use the feedback form if you find anything that is not working correctly or if you have suggestions on improvements.',
            header: 'Feedback',
            button: 'Send feedback',
            'sign-in': 'Sign in to leave feedback.',
        },
    }
};
