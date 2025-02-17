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
  }
}

export default usersApi;
