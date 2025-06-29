import Note from './Note.ts';
import { useEffect, useState } from 'react';
import notesApi from './notesApi.ts';
import ErrorAlert from '../common/ErrorAlert.tsx';
import LoadingSpinner from '../common/LoadingSpinner.tsx';
import NoteComponent from './NoteComponent.tsx';
import LoadableSecondaryButton from '../common/LoadableSecondaryButton.tsx';

interface NotesProps {
}

const Notes = (_: NotesProps) => {
  const [notes, setNotes] = useState<Note[]>([]);
  const [error, setError] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [cursorDaysBack, setCursorDaysBack] = useState<number>(4);

  const getTodaysDate = (): string => {
    const date = new Date();
    return date.toISOString().split('T')[0];
  }

  const getDateDaysAgo = (n: number): string => {
    const date = new Date();
    date.setDate(date.getDate() - Math.abs(n));
    return date.toISOString().split('T')[0];
  }

  const fetchNotes = async (cursorDaysBack: number) => {
    const notes = await notesApi.getNotes(getDateDaysAgo(cursorDaysBack), getTodaysDate());
    setNotes(notes);
  }

  useEffect(() => {
    const fetchInitialNotes = async () => {
      try {
        setIsLoading(true);
        await fetchNotes(cursorDaysBack);
      } catch (e) {
        const errorMessage = (e as Error).message;
        setError(errorMessage);
      }
      setIsLoading(false);
    };

    void fetchInitialNotes();
  }, [cursorDaysBack]);

  async function handleSaveNote(note: Note) {
    try {
      await notesApi.updateNote(note);
      await fetchNotes(cursorDaysBack);
    } catch (e) {
      const errorMessage = (e as Error).message;
      setError(errorMessage);
    }
  }

  return <>
    <h2>Your daily notes</h2>
    {isLoading && <LoadingSpinner />}
    <ErrorAlert errorMessage={error} />
    {!error && (
      <>
        {notes.map((note: Note) => <NoteComponent note={note} onSave={handleSaveNote} key={note.noteId} />)}
        {notes.length > 0 && (
          <div className="text-center mt-6">
            <LoadableSecondaryButton onClick={() => setCursorDaysBack(cursorDaysBack + 5)} isLoading={isLoading} text={`Show older notes`} />
          </div>
        )}
      </>
    )}
  </>;
}
2
export default Notes;