import LoadingSpinner from './LoadingSpinner.tsx';

interface LoadableSubmitButtonProps {
  isLoading: boolean
  text: string
}

const LoadableSubmitButton = (props: LoadableSubmitButtonProps) => {
  return (
  <button type="submit" disabled={props.isLoading} className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 inline-flex items-center">
    {props.isLoading && (
      <LoadingSpinner />
    )}
    {props.text}
  </button>
  )
}

export default LoadableSubmitButton;
