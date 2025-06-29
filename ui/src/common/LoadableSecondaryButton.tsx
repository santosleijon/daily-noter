import LoadingSpinner from './LoadingSpinner.tsx';

interface LoadableSecondaryButtonProps {
  onClick: () => void
  isLoading: boolean
  text: string
}

const LoadableSecondaryButton = (props: LoadableSecondaryButtonProps) => {
  return (
  <button
    disabled={props.isLoading}
    type="button"
    onClick={props.onClick}
    className="text-white bg-cyan-500 hover:bg-blue-600 cursor-pointer focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 inline-flex items-center">
    {props.isLoading && (
      <LoadingSpinner />
    )}
    {props.text}
  </button>
  )
}

export default LoadableSecondaryButton;
