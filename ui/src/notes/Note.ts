interface Note {
  noteId: string
  userId: string
  date: string,
  content: string,
  createdAt: Date
  updatedAt: Date | null
}

export default Note;
