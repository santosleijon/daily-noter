const baseUrl = `http://localhost:8080/api/users`

const usersApi = {
  async login(email: string, password: string) {
    const payload = {
      email: email,
      password: password,
    }

    const response = await fetch(`${baseUrl}/login`, {
      method: 'POST',
      headers: {
        'Accept': '*/*',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload),
    })

    if (!response.ok) {
      throw new Error(`Failed to login (HTTP status = ${response.status})`)
    }
  }
}

export default usersApi;
