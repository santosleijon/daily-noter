import React from 'react';

interface NavigationProps {
  isLoggedIn: boolean
  onLogOut: () => void
}

const Navigation = (props: NavigationProps) => {

  const handleLogout = (e: React.FormEvent) => {
    e.preventDefault();
    props.onLogOut();
  };

  return <nav className="bg-gray-100 shadow-md">
    <div className="container mx-auto px-4 py-3">
      <ul className="flex space-x-6">
        <li><a href="/" className="hover:text-indigo-600">Home</a></li>
        {props.isLoggedIn &&
          <>
            <li><a href="/" className="hover:text-indigo-600" onClick={handleLogout}>Log out</a></li>
          </>
        }
      </ul>
    </div>
  </nav>;
};

export default Navigation;
