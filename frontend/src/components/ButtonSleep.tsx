import { ButtonSleepImg, ButtonSleepSmImg } from "../assets";

interface ButtonProps {
    start: number
    end: number
};

const ButtonSleep = (props: ButtonProps) => {
    return (
        <div className="relative w-full h-15 overflow-hidden backdrop-blur-md">
            <div className="absolute inset-0 flex items-center justify-center">
                <div className="absolute inset-0">
                    <div className="absolute top-0 left-0 right-0 bottom-0 border-1 border-teal-600"></div>

                    <div className="absolute top-0 left-0 w-12 h-4 border-t-3 border-l-3 border-teal-400"></div>
                    <div className="absolute top-0 right-0 w-12 h-4 border-t-3 border-r-3 border-teal-400"></div>
                    <div className="absolute bottom-0 left-0 w-12 h-4 border-b-3 border-l-3 border-teal-400"></div>
                    <div className="absolute bottom-0 right-0 w-12 h-4 border-b-3 border-r-3 border-teal-400"></div>

                    <div className="absolute inset-x-4 w-1/4 centerAbsoluteX top-0 h-2 border-t-2 border-teal-400"></div>
                    <div className="absolute inset-x-4 w-1/4 centerAbsoluteX bottom-0 h-2 border-b-2 border-teal-400"></div>
                </div>

                {props.end - props.start > 3 ? (
                    <>
                        <div className="backLinesGreen"></div>
                    </>
                ) : ""}

                {props.end - props.start > 3 ? (
                    <>
                        <div className="absolute inset-4 flex justify-between items-center">
                            <div className="flex gap-1 items-center opacity-55">
                                <div className="w-[2px] h-10 bg-teal-400"></div>
                                <div className="w-[2px] h-6 bg-teal-400"></div>
                                <div className="w-20 h-[2px] bg-teal-400"></div>
                            </div>
                            <div className="flex gap-1 items-center opacity-55">
                                <div className="w-20 h-[2px] bg-teal-400"></div>
                                <div className="w-[2px] h-6 bg-teal-400"></div>
                                <div className="w-[2px] h-10 bg-teal-400"></div>
                            </div>
                        </div>
                    </>
                ) : ""}

                {/* Иконки */}
                <img src={props.end - props.start > 3 ? ButtonSleepImg : ButtonSleepSmImg} alt="Button Sleep" className="relative z-10" />
            </div>
        </div>
    );
};

export default ButtonSleep;