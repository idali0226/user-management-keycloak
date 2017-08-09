export default {
    blank: '',
    errors: {
        blank: '{{description}} can\'t be blank',
        username: 'Username must be a valid email address', 
        email: 'Must be a valid email address', 
        'invalid-credentials': 'Username or password is invalid',
    },
    definitions: {
        abort: 'Abort',
        continue: 'Continue',
        name: 'Name',  
        date: 'Date',
        'date-descriptive': 'Date (YYYY-MM-DD)',
        user: 'User',
        username: 'Username',
        'username-email': 'Username/email',
        email: 'Email',
        password: 'Password',
        confirmpassword: 'Confirm password',
        usernameOrPassword: 'Username or password',
        'sign-out': 'Sign out',
        'sign-in': 'Sign in',
        'signing-in': 'Signing in', 
        send: 'Send',
        sending: 'Sending',
        next: 'Next',
        previous: 'Previous',   
    },
    main: {
        application_name: 'User management',
        welcome: 'Welcom to DINA User Management System!',
        error: 'An error occured. Please try again.',
        'validation-message': {
            save: 'Could not save',
        },
    }, 
    navigation: {
        start: 'Start', 
        password_recover: 'Forgot password?',
        register: 'Register',
        profile: 'Profile',
        users: 'Users',
        'users.list': 'List all users',
        'users.add': 'Register new',
        clients: 'Clients',
        'clients.list': 'List all clients',
        'clients.add': 'Create new',
    },
    account: {
        new: {
            title: 'Create account',
        }
    },
    user: { 
        list: {
            header: 'List and search for users',
        },
        new: { 
            title: 'New user', 
            toolbar: {
                save: 'Save',
                saving: 'Saving',
                print: 'Print label',
                close: 'Close',
                duplicate: 'Duplicate',
            },
        }, 
        edit: {
            'resign-roles': 'Resign roles:',
        },
        'user-account-filter': 'Filter by user account status: ',
    }, 
    'user-profile': 'User profile',
    'sign-up': "Sign up",
    'edit-user': "Edit user account",
    'register-new-account': 'Register new account',
    fields: {
        labels: {
            user: {
                'first-name': 'First name',
                'last-name': 'Last name',
                name: 'Name',
                email: 'email',
                username: 'Username',
                'username-email': 'Username/Email',
                purpose: 'Purpose',
                descriptions: 'Descriptions',
                'created-date': 'Created date',
                'user-enabled': 'Enabled',
                'email-verified': 'Email verified',
                'account-status': 'Account status',
            }, 
            client: { 
                name: 'Client name', 
            }, 
            role: {
                'realm-role': 'Realm role:',
                'client-roles': 'Client roles'
            }
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
    'sweetalert': {
        'are-you-sure': 'Are you sure?',
        'reject-text': 'Are you sure that you want to reject this user?',
        'reject-text-success': 'User was successfully rejected!',
        'verification-email-text': 'Are you sure that you want to send verification email to this user?',
        'verification-email-success': 'Verification email has been sent to user',
        'email-sent': 'Email sent!',
        'rejected': 'Rejected!',
        'yes-button': 'Yes',
        'cancel-button': 'Cancel',
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
        'saving-account-inprocess': 'Registering user account, please wait!',

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
