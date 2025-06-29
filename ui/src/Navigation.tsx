import React, { useState } from 'react';
import usersApi from './users/usersApi.ts';
import LoadingSpinner from './common/LoadingSpinner.tsx';

interface NavigationProps {
  isLoggedIn: boolean
  onLogoutSuccess: () => Promise<void>
  userEmail: string
}

const Navigation = (props: NavigationProps) => {

  const [logoutIsLoading, setLogoutIsLoading] = useState(false);
  const [logoutErrorMessage, setLogoutErrorMessage] = useState('');

  const handleLogout = async (e: React.FormEvent) => {
    e.preventDefault();
    setLogoutIsLoading(true);

    try {
      await usersApi.logout();
    } catch (e) {
      const errorMessage = (e as Error).message;
      setLogoutErrorMessage(errorMessage)
      setLogoutIsLoading(false);
      return;
    }

    setLogoutIsLoading(false);
    await props.onLogoutSuccess();
  };

  return <nav className="bg-gray-100 shadow-md">
    <div className="container mx-auto px-4 py-3 flex items-center">
      <ul className="flex space-x-4">
        <li><a href="/" className="hover:text-indigo-600">Home</a></li>

        {props.isLoggedIn && (
          <li className="text-gray-600">
            <span className="font-semibold">{props.userEmail}</span>
          </li>
        )}

        {props.isLoggedIn && (
          <>
            <li>
              <a href="/" className="hover:text-indigo-600" onClick={handleLogout}>Log out</a>
              {logoutIsLoading &&
                <div className="inline-block mr-2"><LoadingSpinner color="gray-800" /></div>
              }
              {logoutErrorMessage &&
                <div className="inline-block ml-2 text-red-900 text-xs bg-red-100 rounded-xl py-1 px-3">{logoutErrorMessage}</div>
              }
            </li>
          </>
        )}
      </ul>
    </div>
  </nav>;
};

export default Navigation;
