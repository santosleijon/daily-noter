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

  async updateNote(_: Note): Promise<void> {

  }
}

export default notesApi;