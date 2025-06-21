import Header from './Header.tsx';
import Navigation from './Navigation.tsx';
import Footer from './Footer.tsx';
import { useState } from 'react';
import LoginForm from './users/LoginForm.tsx';
import Notes from './notes/Notes.tsx';

const App = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userEmail, setUserEmail] = useState('');

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
      <Navigation isLoggedIn={isLoggedIn} userEmail={userEmail} onLogoutSuccess={handleLogoutSuccess} />
      <main className="flex-grow container mx-auto px-4 py-8">
        <div className="bg-white rounded-lg shadow-md p-6">
          {isLoggedIn ? (
            <Notes />
          ) : (
            <LoginForm onLoginSuccess={handleLoginSuccess} />
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default App;