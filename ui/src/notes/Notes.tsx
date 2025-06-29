import Note from './Note.ts';
import { useEffect, useState } from 'react';
import notesApi from './notesApi.ts';
import ErrorAlert from '../common/ErrorAlert.tsx';
import LoadingSpinner from '../common/LoadingSpinner.tsx';
import NoteComponent from './NoteComponent.tsx';

interface NotesProps {
}

const Notes = (_: NotesProps) => {
  const [notes, setNotes] = useState<Note[]>([]);
  const [error, setError] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const getTodaysDate = (): string => {
    const date = new Date();
    return date.toISOString().split('T')[0];
  }

  const getDateDaysAgo = (n: number): string => {
    const date = new Date();
    date.setDate(date.getDate() - Math.abs(n));
    return date.toISOString().split('T')[0];
  }

  const fetchNotes = async () => {
    const notes = await notesApi.getNotes(getDateDaysAgo(2), getTodaysDate());
    setNotes(notes);
  }

  useEffect(() => {
    const fetchInitialNotes = async () => {
      try {
        setIsLoading(true);
        await fetchNotes();
      } catch (e) {
        const errorMessage = (e as Error).message;
        setError(errorMessage);
      }
      setIsLoading(false);
    };

    void fetchInitialNotes();
  }, []);

  async function handleSaveNote(note: Note) {
    try {
      await notesApi.updateNote(note);
      await fetchNotes();
    } catch (e) {
      const errorMessage = (e as Error).message;
      setError(errorMessage);
    }
  }

  return <>
    <h2>Your daily notes</h2>
    {isLoading && <LoadingSpinner />}
    <ErrorAlert errorMessage={error} />
    {notes.map((note: Note) => <NoteComponent note={note} onSave={handleSaveNote} key={note.noteId} />)}
  </>;
}

export default Notes;