import Note from './Note.js';
import React, { useState } from 'react';
import LoadableSubmitButton from '../common/LoadableSubmitButton.js';

interface NoteComponentProps {
  note: Note;
  onSave: (note: Note) => Promise<void>;
  key: string
}

const NoteComponent = ({ note, onSave }: NoteComponentProps) => {
  const [content, setContent] = useState(note.content);
  const [saveIsLoading, setSaveIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaveIsLoading(true);
    await onSave({...note, content: content});
    setSaveIsLoading(false);
  };

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

export default NoteComponent;