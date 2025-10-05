import { ButtonMoneyImg, ButtonMoneySmImg } from "../assets";

interface ButtonProps {
    start: number
    end: number
};

const ButtonMoney = (props: ButtonProps) => {
    return (
        <div className="relative w-full h-15  overflow-hidden backdrop-blur-md">
            <div className="absolute inset-0 flex items-center justify-center">
                <div className="absolute inset-0">
                    {/* Внешняя рамка с неравномерной толщиной */}
                    <div className="absolute top-0 left-0 right-0 bottom-0 border border-orange-600"></div>
                    <div className="absolute top-0 left-0 w-12 h-4 border-t-4 border-l-4 border-orange-400"></div>
                    <div className="absolute top-0 right-0 w-12 h-4 border-t-4 border-r-4 border-orange-400"></div>
                    <div className="absolute bottom-0 left-0 w-12 h-4 border-b-4 border-l-4 border-orange-400"></div>
                    <div className="absolute bottom-0 right-0 w-12 h-4 border-b-4 border-r-4 border-orange-400"></div>
                    <div className="absolute inset-x-12 top-0 h-2 border-t-2 border-orange-500"></div>
                    <div className="absolute inset-x-12 bottom-0 h-2 border-b-2 border-orange-500"></div>
                    {/* Диагональные линии */}
                    {props.end - props.start > 3 ? (
                        <>
                            <div className="backLinesOrange"></div>
                        </>
                    ) : ""}

                </div>

                {props.end - props.start > 3 ? (
                    <>
                        <div className="absolute inset-4 flex justify-between items-center">
                            <div className="flex gap-1 items-center opacity-55">
                                <div className="w-[2px] h-10 bg-orange-400"></div>
                                <div className="w-[2px] h-6 bg-orange-400"></div>
                                <div className="w-12 h-[2px] bg-orange-400"></div>
                            </div>
                            <div className="flex gap-1 items-center opacity-55">
                                <div className="w-12 h-[2px] bg-orange-400"></div>
                                <div className="w-[2px] h-6 bg-orange-400"></div>
                                <div className="w-[2px] h-10 bg-orange-400"></div>
                            </div>
                        </div>
                    </>
                ) : ""}


                {/* Иконки */}
                <img src={props.end - props.start > 3 ? ButtonMoneyImg : ButtonMoneySmImg} alt="Button Money" className="relative z-10" />
            </div>
        </div>
    );
};

export default ButtonMoney;