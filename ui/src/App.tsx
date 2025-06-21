import Header from './Header.tsx';
import Navigation from './Navigation.tsx';
import Footer from './Footer.tsx';
import { useEffect, useState } from 'react';
import LoginForm from './users/LoginForm.tsx';
import Notes from './notes/Notes.tsx';
import usersApi from './users/usersApi.ts';
import LoadingSpinner from './common/LoadingSpinner.tsx';
import ErrorAlert from './common/ErrorAlert.tsx';

const App = () => {
  const [getUserSessionIsLoading, setUserSessionIsLoading] = useState(false);
  const [getCurrentSessionErrorMessage, setCurrentSessionErrorMessage] = useState('');
  const [isLoggedIn, setIsLoggedIn] = useState(undefined as boolean | undefined);
  const [userEmail, setUserEmail] = useState('');

  useEffect(() => {
    const getCurrentUserSession = async () => {
      setUserSessionIsLoading(true);

      try {
        const currentUserSession = await usersApi.getCurrentUserSession();

        if (currentUserSession != null) {
          setIsLoggedIn(true);
          setUserEmail(currentUserSession.userEmail);
        } else {
          setIsLoggedIn(false);
        }
      } catch (e) {
        const errorMessage = (e as Error).message;
        setCurrentSessionErrorMessage(errorMessage)
        setUserSessionIsLoading(false);
        return;
      }

      setUserSessionIsLoading(false);
    };

    void getCurrentUserSession();
  }, []);


  async function handleLoginSuccess(email: string) {
    setIsLoggedIn(true);
    setUserEmail(email);
  }

  async function handleLogoutSuccess() {
    setIsLoggedIn(false);
    setUserEmail('');
  }

  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <Navigation isLoggedIn={isLoggedIn == true} userEmail={userEmail} onLogoutSuccess={handleLogoutSuccess} />
      <main className="flex-grow container mx-auto px-4 py-8">
        <div className="bg-white rounded-lg shadow-md p-6">
          {getUserSessionIsLoading &&
            <LoadingSpinner color="gray-800" />
          }
          <ErrorAlert errorMessage={getCurrentSessionErrorMessage} />

          {isLoggedIn === true && <Notes />}
          {isLoggedIn === false && <LoginForm onLoginSuccess={handleLoginSuccess} />}
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default App;