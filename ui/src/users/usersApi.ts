import UserSession from './UserSession.ts';

const baseUrl = `${import.meta.env.VITE_API_URL}/users`

const usersApi = {
  async login(email: string, password: string) {
    const payload = {
      email: email,
      password: password,
    }

    const response = await fetch(`${baseUrl}/login`, {
      method: 'POST',
      credentials: "include",
      headers: {
        'Accept': '*/*',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload),
    })

    if (!response.ok) {
      throw new Error(`Failed to login (HTTP status = ${response.status})`)
    }
  },

  async logout() {
    const response = await fetch(`${baseUrl}/logout`, {
      method: 'POST',
      credentials: "include",
      headers: {
        'Accept': '*/*',
      },
    })

    if (!response.ok) {
      throw new Error(`Failed to logout (HTTP status = ${response.status})`)
    }
  },

  async getCurrentUserSession(): Promise<UserSession | null> {
    const response = await fetch(`${baseUrl}/current-session`, {
      method: 'GET',
      credentials: "include",
    })

    if (response.ok) {
      const responseData = await response.json();

      return {
        sessionId: responseData.sessionId,
        userId: responseData.userId,
        userAgent: responseData.userAgent,
        ipAddress: responseData.ipAddress,
        createdAt: new Date(responseData.createdAt),
        validTo: new Date(responseData.validTo),
        userEmail: responseData.userEmail,
      }
    } else if (response.status === 400 || response.status === 401 || response.status === 403) {
      return null;
    } else if (!response.ok) {
      throw new Error(`Failed to login (HTTP status = ${response.status})`);
    }

    return null;
  },
}

export default usersApi;
