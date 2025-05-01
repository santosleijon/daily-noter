import Note from './Note.ts';

const baseUrl = `${import.meta.env.VITE_API_URL}/notes`

const notesApi = {
  async getNotes(fromDate: string, toDate: string): Promise<Note[]> {
    const queryParams = new URLSearchParams({
      from: fromDate,
      to: toDate,
    }).toString()

    const response = await fetch(`${baseUrl}?${queryParams}`, {
      credentials: "include"
    })

    if (!response.ok) {
      throw new Error(`Failed to retrieve notes (HTTP status = ${response.status})`)
    }

    const responseData = await response.json();

    return responseData.map((item: any) => ({
      noteId: item.noteId,
      userId: item.userId,
      date: item.date,
      content: item.content,
      createdAt: new Date(item.createdAt),
      updatedAt: item.updatedAt ? new Date(item.updatedAt) : null
    }));
  },

  async updateNote(note: Note): Promise<void> {
    const payload = {
      content: note.content,
    }

    const response = await fetch(`${baseUrl}/${note.noteId}`, {
      method: 'POST',
      credentials: "include",
      headers: {
        'Accept': '*/*',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload),
    })

    if (!response.ok) {
      throw new Error(`Failed to update note (HTTP status = ${response.status})`)
    }
  }
}

export default notesApi;