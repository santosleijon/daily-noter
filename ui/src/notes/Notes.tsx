import Note from './Note.ts';
import React, { useEffect, useState } from 'react';
import LoadableSubmitButton from '../common/LoadableSubmitButton.tsx';
import notesApi from './notesApi.ts';
import ErrorAlert from '../common/ErrorAlert.tsx';
import LoadingSpinner from '../common/LoadingSpinner.tsx';

interface NotesProps {
}

const Notes = (_: NotesProps) => {
  const [notes, setNotes] = useState<Note[]>([]);
  const [error, setError] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchNotes = async () => {
      try {
        setIsLoading(true);
        const retrievedNotes = await notesApi.getNotes("2025-01-01", "2025-01-03"); // TODO: Use today's date
        setNotes(retrievedNotes);
        setIsLoading(false);
      } catch (e) {
        const errorMessage = (e as Error).message;
        setError(errorMessage)
        setIsLoading(false);
        return;
      }
    }

    void fetchNotes();
  }, []);

  async function handleSaveNote(_: Note) {
    // TODO: Make API call to save note
  }

  return <>
    <h2>Your daily notes</h2>
    {notes.map((note: Note) => <NoteComponent note={note} onSave={handleSaveNote} key={note.noteId} />)}
    <ErrorAlert errorMessage={error} />
    {isLoading && <LoadingSpinner />}
  </>;
}

interface NoteProps {
  note: Note;
  onSave: (note: Note) => Promise<void>;
  key: string
}

const NoteComponent = ({ note, onSave }: NoteProps) => {
  const [content, setContent] = useState(note.content);
  const [saveIsLoading, setSaveIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaveIsLoading(true);
    await onSave({...note, content: content});
    setSaveIsLoading(false);
  };

  console.log("note.createdAt", typeof note.createdAt);

  return (
    <div>
      <div className="mt-8 flex items-center">
        <h3>{note.date}</h3>
        <div className="text-xs ml-2 mb-1 text-cyan-800">
          Created {note.createdAt.toISOString()}{note.updatedAt && <>, updated {note.updatedAt.toISOString()}</>}
        </div>
      </div>
      <form onSubmit={handleSubmit}>
        <textarea
          className="block mb-3 p-2 w-full text-sm text-gray-900 rounded border-solid field-sizing-content overflow-hidden"
          rows={5}
          onChange={(e) => setContent(e.target.value)}
          defaultValue={note.content} />
        <LoadableSubmitButton isLoading={saveIsLoading} text="Save" />
      </form>
    </div>
  )
}

export default Notes;