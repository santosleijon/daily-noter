import Header from './Header.tsx';
import Navigation from './Navigation.tsx';
import Footer from './Footer.tsx';
import { useState } from 'react';
import LoginForm from './users/LoginForm.tsx';

const App = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userEmail, setUserEmail] = useState('');

  function handleLoginSuccess(email: string) {
    setIsLoggedIn(true);
    setUserEmail(email);
  }

  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <Navigation />
      <main className="flex-grow container mx-auto px-4 py-8">
        <div className="bg-white rounded-lg shadow-md p-6">
          {isLoggedIn ? (
            <>
              <h2>Welcome, {userEmail}</h2>
              <p className="text-gray-600">This is where you'll be writing your daily notes...</p>
            </>
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