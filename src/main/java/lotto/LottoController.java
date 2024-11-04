package lotto;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import lotto.domain.Lotto;
import lotto.domain.Rank;
import lotto.service.LottoResultAnalysisService;
import lotto.service.LottoSalesService;
import lotto.view.InputView;
import lotto.view.OutputView;

public class LottoController {

    private final InputView inputView;
    private final OutputView outputView;
    private final LottoSalesService salesService;
    private LottoResultAnalysisService resultAnalysisService;

    public LottoController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.salesService = new LottoSalesService();
        this.resultAnalysisService = null;
    }

    public void run() {
        // TODO: Input이 Null인지 확인!!
        int payment;
        List<Lotto> lottos;
        while (true) {
            outputView.printPaymentRequestMessage();
            payment = inputView.readPayment();
            try {
                int quantity = salesService.getAvailableLottoQuantity(payment);
                lottos = salesService.createLottos(quantity);
                outputView.printLottoDetails(lottos);
                break;
            } catch (IllegalArgumentException e) {
                OutputView.printExceptionMessage(e.getMessage());
            }
        }

        String numbers;
        int bonusNumber;
        List<Integer> winningNumbers;
        List<Rank> winningResults;
        List<Integer> winningStatistics;
        String lottoProfitRate;
        while (true) {
            outputView.printWinningNumbersRequestMessage();
            numbers = inputView.readWinningNumbers();

            outputView.printBonusNumberRequestMessage();
            bonusNumber = inputView.readBonusNumber();

            try {
                winningNumbers = createWinningNumbers(numbers);

                // TODO: 개선 요망
                setLottoResultAnalysisService(winningNumbers, bonusNumber);
                winningResults = resultAnalysisService.generateWinningResults(lottos);

                winningStatistics = resultAnalysisService.getWinningStatistics(winningResults);
                lottoProfitRate = resultAnalysisService.getLottoProfitRate(winningResults, payment);
                outputView.printWinningResults(winningStatistics, lottoProfitRate);
                break;
            } catch (IllegalArgumentException e) {
                OutputView.printExceptionMessage(e.getMessage());
            } catch (Exception e) {
                OutputView.printExceptionMessage("예기치 못한 예외가 발생하였습니다.");
            }
        }

        inputView.closeConsole();
    }

    private void setLottoResultAnalysisService(List<Integer> winningLottoNumbers, int bonusNumber) {
        if (resultAnalysisService == null) {
            resultAnalysisService = new LottoResultAnalysisService(winningLottoNumbers, bonusNumber);
        }
    }

    private List<Integer> createWinningNumbers(String numbers) {
        List<String> splitNumbers = Arrays.stream(numbers.split(","))
            .map(String::trim).toList();
        try {
            return splitNumbers.stream().map(Integer::parseInt).toList();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("당첨 번호는 정수여야 합니다.");
        }
    }
}
