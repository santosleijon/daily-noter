import React, { useState } from 'react';
import LoadableSubmitButton from '../common/LoadableSubmitButton.tsx';

interface LoginFormProps {
  onLoginSuccess: (email: string) => Promise<void>;
}

const LoginForm = (props: LoginFormProps) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loginIsLoading, setLoginIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoginIsLoading(true);
    await props.onLoginSuccess(email);
    setLoginIsLoading(false);
  };

  return (
    <>
      <h2>Log in to start writing today's notes</h2>
      <form onSubmit={handleSubmit} className="w-full max-w-sm">
        <div className="mb-4">
          <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
            Email
          </label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            className="w-full px-4 py-2 border rounded-lg shadow-sm focus:ring-blue-500 focus:border-blue-500 border-gray-300"
          />
        </div>
        <div className="mb-6">
          <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
            Password
          </label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            className="w-full px-4 py-2 border rounded-lg shadow-sm focus:ring-blue-500 focus:border-blue-500 border-gray-300"
          />
        </div>
        <LoadableSubmitButton isLoading={loginIsLoading} text="Login" />
      </form>
    </>
  );
};

export default LoginForm;
